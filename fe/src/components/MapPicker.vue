<template>
    <div>
      <h2>Chọn địa chỉ giao hàng</h2>
      <div id="map" style="height: 400px; width: 100%"></div>
      <input type="text" v-model="selectedAddress" readonly placeholder="Địa chỉ được chọn sẽ hiển thị ở đây" />
      <button @click="useAddress">Sử dụng địa chỉ này</button>
    </div>
  </template>
  
  <script lang="ts">
  export default {
    name: 'MapPicker',
    data() {
      return {
        selectedAddress: '',
        map: null,
        marker: null,
        isGoogleMapsLoaded: false
      };
    },
    mounted() {
      if (window.google) {
        // Nếu API đã tải, khởi tạo ngay
        this.initMap();
      } else {
        // Chờ API tải xong
        window.addEventListener('googleMapsLoaded', () => {
          this.isGoogleMapsLoaded = true;
          this.initMap();
        });
      }
    },
    methods: {
      initMap() {
        if (!this.isGoogleMapsLoaded && !window.google) {
          console.error('Google Maps API chưa được tải');
          return;
        }
  
        // Khởi tạo bản đồ với vị trí mặc định (Hà Nội)
        this.map = new google.maps.Map(document.getElementById('map'), {
          center: { lat: 21.0285, lng: 105.8542 }, // Hà Nội
          zoom: 12
        });
  
        // Thêm sự kiện nhấp chuột vào bản đồ
        this.map.addListener('click', (event) => {
          const lat = event.latLng.lat();
          const lng = event.latLng.lng();
  
          // Cập nhật hoặc tạo marker
          if (this.marker) {
            this.marker.setPosition(event.latLng);
          } else {
            this.marker = new google.maps.Marker({
              position: event.latLng,
              map: this.map
            });
          }
  
          // Reverse geocoding để lấy địa chỉ
          const geocoder = new google.maps.Geocoder();
          geocoder.geocode({ location: { lat, lng } }, (results, status) => {
            if (status === 'OK' && results[0]) {
              this.selectedAddress = results[0].formatted_address;
            } else {
              this.selectedAddress = 'Không tìm thấy địa chỉ';
            }
          });
        });
      },
      useAddress() {
        if (this.selectedAddress && this.selectedAddress !== 'Không tìm thấy địa chỉ') {
          alert('Địa chỉ được chọn: ' + this.selectedAddress);
          // Truyền địa chỉ vào biểu mẫu hoặc backend
          this.$emit('address-selected', this.selectedAddress);
        } else {
          alert('Vui lòng chọn một địa chỉ hợp lệ trên bản đồ');
        }
      }
    }
  };
  </script>
  
  <style scoped>
  #map {
    height: 400px;
    width: 100%;
  }
  input {
    margin-top: 10px;
    padding: 10px;
    width: 100%;
    font-size: 16px;
  }
  </style>