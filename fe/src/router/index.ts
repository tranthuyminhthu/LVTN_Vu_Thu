import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import NotFoundPage from "@/page/NotFoundPage.vue";
import HomePage from "@/page/HomePage.vue";
import LoginPage from "@/page/LoginPage.vue";
import ProductListPage from "@/page/ProductListPage.vue";
import SignUpPage from "@/page/SignUpPage.vue";
import ProductPage from "@/page/ProductPage/ProductPage.vue";
import HeaderLayout from "@/layouts/HeaderLayout.vue";
import FullWidth from "@/layouts/FullWidth.vue";
import CartPage from "@/page/CartPage.vue";
import AccountPage from "@/page/AccountPage.vue";
import InfoPage from "@/page/account/InfoPage.vue";
import OrderPage from "@/page/account/OrderPage.vue";
import AddressPage from "@/page/account/AddressPage.vue";
import OrderSuccessPage from "@/page/OrderSuccessPage.vue";
import VoucherPage from "@/page/account/VoucherPage.vue";
import SpotlightPage from "@/page/SpotlightPage.vue";
import OrderDetailPage from "@/page/OrderDetailPage.vue";
import MessagePage from "@/page/MessagePage.vue";
import AdminLayout from "@/layouts/AdminLayout.vue";
import DashboardPage from "@/page/AdminPage/DashboardPage.vue";
import UserPage from "@/page/AdminPage/UserPage.vue";
import ProductPageAdmin from "@/page/AdminPage/ProductPage.vue";
import OrderPageAdmin from "@/page/AdminPage/OrderPage.vue";
import FinancePage from "@/page/AdminPage/FinancePage.vue";
import VendorLayout from "@/layouts/VendorLayout.vue";
import StoreManagement from "@/page/Vendors/StoreManagement.vue";
import ProductManagement from "@/page/Vendors/ProductManagement.vue";
import OrderManagement from "@/page/Vendors/OrderManagement.vue";
import FinanceManagement from "@/page/Vendors/FinanceManagement.vue";
import AdvertisementManagement from "@/page/Vendors/AdvertisementManagement.vue";
import CustomerSupport from "@/page/Vendors/CustomerSupport.vue";
import AIRecommendationPage from "@/page/AIRecommendationPage.vue";
import FavoriteProductsPage from "@/page/account/FavoriteProductsPage.vue";
import ChatPage from "@/page/ChatPage.vue";
import OnlyHeaderLayout from "@/layouts/OnlyHeaderLayout.vue";
import { useAuthStore } from "@/store";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: FullWidth,
      children: [
        {
          path: "",
          name: "home",
          component: HomePage,
        },
        {
          path: "products",
          name: "product-list",
          component: ProductListPage,
        },
        {
          path: "cart",
          name: "cart",
          component: CartPage,
        },
        {
          path: "order-success",
          name: "order-success",
          component: OrderSuccessPage,
        },
        {
          path: "spotlight",
          name: "spotlight",
          component: SpotlightPage,
        },
        {
          path: "message",
          name: "message",
          component: MessagePage,
        },
        {
          path: "ai-recommendation",
          name: "vendor-ai-recommendation",
          component: AIRecommendationPage,
        },
        {
          path: "account",
          name: "account",
          component: AccountPage,
          children: [
            {
              path: "info",
              name: "info",
              component: InfoPage,
            },
            {
              path: "orders",
              name: "orders",
              component: OrderPage,
            },
            {
              path: "orders/:id",
              name: "order-detail",
              component: OrderDetailPage,
            },
            {
              path: "address",
              name: "address",
              component: AddressPage,
            },
            {
              path: "voucher",
              name: "voucher",
              component: VoucherPage,
            },
            {
              path: "favorites",
              name: "favorites",
              component: FavoriteProductsPage,
            },
          ],
        },
      ],
    },
    {
      path: "/about",
      name: "about",
      component: () => import("../views/AboutView.vue"),
    },
    {
      path: "/login",
      name: "login",
      component: LoginPage,
    },
    {
      path: "/signup",
      name: "signup",
      component: SignUpPage,
    },
    {
      path: "/product/:id",
      component: HeaderLayout,
      children: [
        {
          path: "",
          name: "product",
          component: ProductPage,
        },
      ],
    },
    {
      path: "/admin",
      component: AdminLayout,
      children: [
        {
          path: "dashboard",
          name: "dashboard",
          component: DashboardPage,
        },
        {
          path: "users",
          name: "users",
          component: UserPage,
        },
        {
          path: "products",
          name: "products",
          component: ProductPageAdmin,
        },
        {
          path: "orders",
          name: "orders-admin",
          component: OrderPageAdmin,
        },
        {
          path: "finance",
          name: "finance",
          component: FinancePage,
        },
      ],
    },
    {
      path: "/vendor",
      component: VendorLayout,
      meta: {
        requiresAuth: true,
        roles: ['VENDOR'],
      },
      children: [
        {
          path: "dashboard",
          name: "vendor-dashboard",
          component: StoreManagement,
        },
        {
          path: "products",
          name: "vendor-products",
          component: ProductManagement,
        },
        {
          path: "orders",
          name: "vendor-orders",
          component: OrderManagement,
        },
        {
          path: "finance",
          name: "vendor-finance",
          component: FinanceManagement,
        },
        {
          path: "advertisement",
          name: "vendor-advertisement",
          component: AdvertisementManagement,
        },
        {
          path: "support",
          name: "vendor-support",
          component: CustomerSupport,
        },
      ],
    },
    {
      path: "/chat",
      component: OnlyHeaderLayout,
      children: [
        {
          path: ":id?",
          name: "chat",
          component: ChatPage,
        },
      ],
    },
    {
      path: "/:pathMatch(.*)*",
      name: "not-found-catch-all",
      component: NotFoundPage,
    },
  ],
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  // // Kiểm tra nếu route yêu cầu authentication
  // if (to.meta.requiresAuth) {
  //   if (!authStore.user) {
  //     // Nếu chưa đăng nhập, redirect về login
  //     next('/login');
  //     return;
  //   }
    
  //   // Kiểm tra role nếu có yêu cầu
  //   if (to.meta.roles && Array.isArray(to.meta.roles)) {
  //     const hasRequiredRole = to.meta.roles.some((role: string) => 
  //       authStore.hasRole(role)
  //     );
      
  //     if (!hasRequiredRole) {
  //       // Nếu không có role phù hợp, redirect về home
  //       next('/');
  //       return;
  //     }
  //   }
  // }
  
  // // Nếu đã đăng nhập và cố gắng truy cập login/signup, redirect về home
  // if (authStore.user && (to.name === 'login' || to.name === 'signup')) {
  //   next('/');
  //   return;
  // }
  
  next();
});

export default router;
