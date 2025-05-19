import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import NotFoundPage from '@/page/NotFoundPage.vue'
import HomePage from '@/page/HomePage.vue'
import LoginPage from '@/page/LoginPage.vue'
import ProductListPage from '@/page/ProductListPage.vue'
import SignUpPage from '@/page/SignUpPage.vue'
import ProductPage from '@/page/ProductPage.vue'
import HeaderLayout from '@/layouts/HeaderLayout.vue'
import FullWidth from '@/layouts/FullWidth.vue'
import CartPage from '@/page/CartPage.vue'
import AccountPage from '@/page/AccountPage.vue'
import InfoPage from '@/page/account/InfoPage.vue'
import OrderPage from '@/page/account/OrderPage.vue'
import AddressPage from '@/page/account/AddressPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignUpPage,
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found-catch-all',
      component: NotFoundPage
    },
    {
      path: '/product',
      component: HeaderLayout,
      children: [
        {
          path: '',
          name: 'product',
          component: ProductPage,
        },
      ]
    },
    {
      path: '/',
      component: FullWidth,
      children: [
        {
          path: 'products',
          name: 'product-list',
          component: ProductListPage,
        },
        {
          path: 'cart',
          name: 'cart',
          component: CartPage,
        },
        {
          path: 'account',
          name: 'account',
          component: AccountPage,
          children: [
            {
              path: 'info',
              name: 'info',
              component: InfoPage,
            },
            {
              path: 'orders',
              name: 'orders',
              component: OrderPage,
            },
            {
              path: 'address',
              name: 'address',
              component: AddressPage,
            },
          ]
        },
      ]
    },
  ],
})

export default router
