
<template>
  <footer class="!bg-black text-white">
    <!-- Top Section -->
    <div class="!max-w-8xl !mx-auto !px-4 !py-8 ">
      <div class="!grid grid-cols-1 lg:grid-cols-3 gap-8 !mb-12">
        <!-- Feedback Section -->
        <div class="lg:col-span-1">
          <h2 class="text-2xl font-bold mb-2 text-start">COOLMATE lắng nghe bạn!</h2>
          <p class="text-gray-300 mb-2 text-sm leading-relaxed text-start">
            Chúng tôi luôn trân trọng và mong muốn nhận được mọi phản hồi từ khách hàng để có thể nâng cấp trải nghiệm dịch vụ và sản phẩm ngày càng tốt hơn.
          </p>
          <button class="!bg-white !text-black !px-6 !py-3 !rounded-full !font-medium flex items-center gap-2 hover:bg-gray-100 transition-colors">
            ĐÓNG GÓP Ý KIẾN
            <svg class="!w-4 !h-4" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clip-rule="evenodd" />
            </svg>
          </button>
        </div>

        <!-- Contact Information -->
        <div class="lg:col-span-1 flex flex-col">
          <div class="flex items-center gap-3 mb-4">
            <svg class="!w-5 !h-5" fill="currentColor" viewBox="0 0 20 20">
              <path d="M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z" />
            </svg>
            <span class="font-medium">Hotline</span>
            <span class="text-gray-300">1900.272737 - 028.7777.2737</span>
          </div>
          <div class="flex items-center gap-3">
            <svg class="!w-5 !h-5" fill="currentColor" viewBox="0 0 20 20">
              <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z" />
              <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z" />
            </svg>
            <span class="font-medium">Email</span>
            <span class="text-gray-300">Cool@coolmate.me</span>
          </div>
        </div>

        <!-- Social Media -->
        <div class="lg:col-span-1 flex lg:justify-end">
          <div class="flex gap-3">
            <a href="#" class="!w-10 !h-10 border border-white rounded-lg flex items-center justify-center hover:bg-white hover:text-black transition-colors">
              <span class="font-bold text-sm">f</span>
            </a>
            <a href="#" class="!w-10 !h-10 border border-white rounded-lg flex items-center justify-center hover:bg-white hover:text-black transition-colors">
              <span class="font-bold text-sm">Z</span>
            </a>
            <a href="#" class="!w-10 !h-10 border border-white rounded-lg flex items-center justify-center hover:bg-white hover:text-black transition-colors">
              <span class="font-bold text-sm">T</span>
            </a>
            <a href="#" class="!w-10 !h-10 border border-white rounded-lg flex items-center justify-center hover:bg-white hover:text-black transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clip-rule="evenodd" />
              </svg>
            </a>
            <a href="#" class="!w-10 !h-10 border border-white rounded-lg flex items-center justify-center hover:bg-white hover:text-black transition-colors">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clip-rule="evenodd" />
              </svg>
            </a>
          </div>
        </div>
      </div>

      <!-- Main Footer Content -->
      <div class="!grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-8 mb-12">
        <!-- COOLCLUB -->
        <div class="text-start">
          <h3 class="font-bold text-lg mb-4">COOLCLUB</h3>
          <ul class="space-y-2 text-sm text-gray-300">
            <li><a href="#" class="hover:text-white transition-colors">Tài khoản CoolClub</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Đăng kí thành viên</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Ưu đãi & Đặc quyền</a></li>
          </ul>
        </div>

        <!-- CHÍNH SÁCH -->
        <div class="text-start">
          <h3 class="font-bold text-lg mb-4">CHÍNH SÁCH</h3>
          <ul class="space-y-2 text-sm text-gray-300">
            <li><a href="#" class="hover:text-white transition-colors">Chính sách đổi trả 60 ngày</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Chính sách khuyến mãi</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Chính sách bảo mật</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Chính sách giao hàng</a></li>
          </ul>
        </div>

        <!-- CHĂM SÓC KHÁCH HÀNG -->
        <div class="text-start">
          <h3 class="font-bold text-lg mb-4">CHĂM SÓC KHÁCH HÀNG</h3>
          <ul class="space-y-2 text-sm text-gray-300">
            <li><a href="#" class="hover:text-white transition-colors">Trải nghiệm mua sắm 100% hài lòng</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Hỏi đáp - FAQs</a></li>
          </ul>
        </div>

        <!-- KIẾN THỨC MẶC ĐẸP -->
        <div class="text-start">
          <h3 class="font-bold text-lg mb-4">KIẾN THỨC MẶC ĐẸP</h3>
          <ul class="space-y-2 text-sm text-gray-300">
            <li><a href="#" class="hover:text-white transition-colors">Hướng dẫn chọn size</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Blog</a></li>
          </ul>
        </div>

        <!-- VỀ COOLMATE -->
        <div class="text-start">
          <h3 class="font-bold text-lg mb-4">VỀ COOLMATE</h3>
          <ul class="space-y-2 text-sm text-gray-300">
            <li><a href="#" class="hover:text-white transition-colors">Quy tắc ứng xử của Coolmate</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Coolmate 101</a></li>
            <li><a href="#" class="hover:text-white transition-colors">DVKH xuất sắc</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Câu chuyện về Coolmate</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Nhà máy</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Care & Share</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Cam kết bền vững</a></li>
            <li><a href="#" class="hover:text-white transition-colors">Tầm nhìn 2030</a></li>
          </ul>
        </div>

        <!-- ĐỊA CHỈ LIÊN HỆ -->
        <div class="text-start">
          <h3 class="font-bold text-lg mb-4">ĐỊA CHỈ LIÊN HỆ</h3>
          <div class="space-y-3 text-sm text-gray-300">
            <div>
              <p class="font-medium text-white mb-1">Văn phòng Hà Nội:</p>
              <p>Tầng 3-4, Tòa nhà BMM, Km2, Đường Phùng Hưng, Phường Hà Đông, Thành phố Hà Nội, Việt Nam</p>
            </div>
            <div>
              <p class="font-medium text-white mb-1">Trung tâm vận hành Hà Nội:</p>
              <p>Lô C8, KCN Lại Yên, Xã Lại Yên, Huyện Hoài Đức, Thành phố Hà Nội</p>
            </div>
            <div>
              <p class="font-medium text-white mb-1">Văn phòng và Trung tâm vận hành TP.HCM:</p>
              <p>Lô C3, đường D2, KCN Cát Lái, Thạnh Mỹ Lợi, TP. Thủ Đức, TP. Hồ Chí Minh</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bottom Section -->
    <div class="border-t border-gray-800 py-6">
      <div class="max-w-8xl mx-auto px-4">
        <div class="flex flex-col lg:flex-row justify-between items-center gap-4">
          <!-- Copyright -->
          <div class="text-sm text-gray-400 text-center lg:text-left">
            <p class="mb-1">© CÔNG TY TNHH FASTECH ASIA</p>
            <p>Mã số doanh nghiệp: 0108617038. Giấy chứng nhận đăng ký doanh nghiệp do Sở Kế hoạch và Đầu tư TP Hà Nội cấp lần đầu ngày 20/02/2019.</p>
          </div>

          <!-- Certifications -->
          <div class="flex gap-4 ">
            <div class="flex items-center gap-2 text-xs text-gray-400">
              <div class="w-8 h-8 bg-blue-600 rounded flex items-center justify-center">
                <span class="text-white font-bold text-xs">NCSC</span>
              </div>
              <span>Website đạt chứng nhận TÍN NHIỆM MẠNG</span>
            </div>
            <div class="flex items-center gap-2 text-xs text-gray-400">
              <div class="w-8 h-8 bg-gray-700 rounded flex items-center justify-center">
                <span class="text-white font-bold text-xs">DMCA</span>
              </div>
              <span>PROTECTED BY DMCA www.DMCA.com</span>
            </div>
            <div class="flex items-center gap-2 text-xs text-gray-400">
              <div class="w-8 h-8 bg-green-600 rounded-full flex items-center justify-center">
                <span class="text-white font-bold text-xs">HQC</span>
              </div>
              <span>207.21</span>
            </div>
            <div class="flex items-center gap-2 text-xs text-gray-400">
              <div class="w-8 h-8 bg-blue-500 rounded flex items-center justify-center">
                <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                </svg>
              </div>
              <span>ĐÃ THÔNG BÁO BỘ CÔNG THƯƠNG</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Floating Elements -->
    <!-- Scroll to Top Button -->
    <button class="fixed bottom-6 left-6 !w-12 !h-12 bg-blue-600 text-white rounded-full flex items-center justify-center hover:bg-blue-700 transition-colors z-50" @click="scrollToTop">
      <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
        <path fill-rule="evenodd" d="M3.293 9.707a1 1 0 010-1.414l6-6a1 1 0 011.414 0l6 6a1 1 0 01-1.414 1.414L11 5.414V17a1 1 0 11-2 0V5.414L4.707 9.707a1 1 0 01-1.414 0z" clip-rule="evenodd" />
      </svg>
    </button>
  </footer>
</template>

<script setup lang="ts">
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};
</script>

<style scoped>
/* Custom scrollbar for better UX */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>
