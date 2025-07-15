<template>
  <div class="p-4">
    <h2 class="text-xl font-bold mb-4">Chọn địa chỉ giao hàng</h2>
    
    <!-- Form thông tin người nhận -->
    <div class="mb-4 p-4 bg-gray-50 rounded-lg">
      <h3 class="font-semibold mb-3">Thông tin người nhận</h3>
      <div class="grid grid-cols-2 gap-3">
        <div>
          <label class="block text-sm font-medium mb-1">Họ tên</label>
          <input 
            type="text" 
            v-model="recipientInfo.name" 
            placeholder="Nhập họ tên"
            class="w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Số điện thoại</label>
          <input 
            type="tel" 
            v-model="recipientInfo.phone" 
            placeholder="Nhập số điện thoại"
            class="w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
        <div class="col-span-2">
          <label class="block text-sm font-medium mb-1">Email</label>
          <input 
            type="email" 
            v-model="recipientInfo.email" 
            placeholder="Nhập email"
            class="w-full p-2 border border-gray-300 rounded-md"
          />
        </div>
      </div>
    </div>

    <!-- Bản đồ -->
    <div id="map" style="height: 400px; width: 100%; border-radius: 8px; margin-bottom: 16px;"></div>
    
    <!-- Địa chỉ được chọn -->
    <div class="mb-4">
      <label class="block text-sm font-medium mb-2">Địa chỉ được chọn:</label>
      <input 
        type="text" 
        v-model="selectedAddress" 
        readonly 
        placeholder="Địa chỉ sẽ hiển thị ở đây sau khi chọn trên bản đồ" 
        class="w-full p-3 border border-gray-300 rounded-md bg-gray-50"
      />
    </div>

    <!-- Nút sử dụng địa chỉ -->
    <div class="flex gap-3">
      <button 
        @click="useAddress" 
        class="flex-1 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
        :disabled="!selectedAddress || selectedAddress === 'Không tìm thấy địa chỉ'"
      >
        Sử dụng địa chỉ này
      </button>
      <button 
        @click="clearSelection" 
        class="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
      >
        Xóa
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import mapboxgl from 'mapbox-gl'

// Định nghĩa emit
const emit = defineEmits<{
  'address-selected': [data: { address: string, name?: string, phone?: string, email?: string }]
}>()

interface RecipientInfo {
  name: string
  phone: string
  email: string
}

interface MapFeatureProperties {
  name?: string
  [key: string]: unknown
}

const MAPBOX_TOKEN = import.meta.env.VITE_MAPBOX_TOKEN
const selectedAddress = ref('')
const recipientInfo = ref<RecipientInfo>({
  name: '',
  phone: '',
  email: ''
})

let map: mapboxgl.Map | null = null
let marker: mapboxgl.Marker | null = null

onMounted(() => {
  mapboxgl.accessToken = MAPBOX_TOKEN
  map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/streets-v12',
    center: [106.7009, 10.7769], // [lng, lat] - TP.HCM
    zoom: 15,
    pitch: 60,    // Góc nghiêng để nhìn 3D
    bearing: -17.6 // Góc xoay
  })

  map.on('click', async (e) => {
    const { lng, lat } = e.lngLat
    if (marker) {
      marker.setLngLat([lng, lat])
    } else {
      marker = new mapboxgl.Marker().setLngLat([lng, lat]).addTo(map!)
    }
    // Reverse geocoding
    const res = await fetch(`https://api.mapbox.com/geocoding/v5/mapbox.places/${lng},${lat}.json?access_token=${MAPBOX_TOKEN}`)
    const data = await res.json()
    if (data.features && data.features[0]) {
      console.log(data.features[0])
      selectedAddress.value = data.features[0].place_name
    } else {
      selectedAddress.value = 'Không tìm thấy địa chỉ'
    }
  })

  map.on('load', () => {
    if (!map) return;
    // Thêm layer 3D buildings
    const layers = map.getStyle().layers;
    let labelLayerId;
    for (const layer of layers) {
      if (layer.type === 'symbol' && layer.layout && layer.layout['text-field']) {
        labelLayerId = layer.id;
        break;
      }
    }
    map.addLayer(
      {
        'id': '3d-buildings',
        'source': 'composite',
        'source-layer': 'building',
        'filter': ['==', 'extrude', 'true'],
        'type': 'fill-extrusion',
        'minzoom': 15,
        'paint': {
          'fill-extrusion-color': '#aaa',
          'fill-extrusion-height': [
            'interpolate', ['linear'], ['zoom'],
            15, 0,
            15.05, ['get', 'height']
          ],
          'fill-extrusion-base': [
            'interpolate', ['linear'], ['zoom'],
            15, 0,
            15.05, ['get', 'min_height']
          ],
          'fill-extrusion-opacity': 0.6
        }
      },
      labelLayerId
    );
    // Lắng nghe click vào POI (địa điểm có sẵn)
    map.on('click', 'poi-label', function (e) {
      if (e.features && e.features.length > 0) {
        const feature = e.features[0];
        const name = (feature.properties as MapFeatureProperties)?.name || 'Không rõ tên';
        const geometry = feature.geometry;
        if (geometry.type === 'Point' && geometry.coordinates) {
          const coords = geometry.coordinates as [number, number];
          // Cập nhật marker về đúng vị trí POI
          if (marker) {
            marker.setLngLat(coords);
          } else {
            marker = new mapboxgl.Marker().setLngLat(coords).addTo(map!);
          }
          new mapboxgl.Popup()
            .setLngLat(coords)
            .setHTML(`<strong>${name}</strong>`)
            .addTo(map!);
          selectedAddress.value = name;
        }
      }
    });
    map.on('mouseenter', 'poi-label', () => {
      if (map) map.getCanvas().style.cursor = 'pointer';
    });
    map.on('mouseleave', 'poi-label', () => {
      if (map) map.getCanvas().style.cursor = '';
    });
  });
})

onBeforeUnmount(() => {
  if (map) map.remove()
})

function clearSelection() {
  selectedAddress.value = ''
  recipientInfo.value = {
    name: '',
    phone: '',
    email: ''
  }
  if (marker) {
    marker.remove()
    marker = null
  }
}

function useAddress() {
  if (selectedAddress.value && selectedAddress.value !== 'Không tìm thấy địa chỉ') {
    // Emit object chứa tất cả thông tin
    const addressData = {
      address: selectedAddress.value,
      name: recipientInfo.value.name,
      phone: recipientInfo.value.phone,
      email: recipientInfo.value.email
    }
    
    emit('address-selected', addressData)
  } else {
    alert('Vui lòng chọn một địa chỉ hợp lệ trên bản đồ')
  }
}
</script>

<style scoped>
#map {
  height: 400px;
  width: 100%;
}
</style>