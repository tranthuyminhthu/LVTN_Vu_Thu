# Fashion E-Commerce System with AI Integration

A modern fashion business system that integrates AI technologies to optimize user experience and product recommendations, built with a Microservice Architecture to enhance scalability, maintainability, and deployment flexibility.

![Workflow System](https://i.imgur.com/QFB6cQ5.png)

## 🏗️ System Architecture

This project follows a **Microservice Architecture** with the following components:

### Backend Services
- **API Gateway** (Spring Cloud Gateway) - Port 8888
  - Central entry point for all client requests
  - Route management and request forwarding
  - Load balancing and service discovery

- **Products Service** (Spring Boot) - Port 8080
  - Product management and catalog
  - Product variants (size, color, stock)
  - MongoDB integration for data persistence

- **Main Application** (Spring Boot) - Port 8080
  - Core business logic
  - User management and authentication
  - Order processing and payment integration

### Frontend Application
- **Vue.js 3** with TypeScript
- **PrimeVue** UI components
- **Tailwind CSS** for styling
- **Pinia** for state management
- **Vue Router** for navigation
- **Socket.IO** for real-time communication

## 🚀 Key Features

### Customer Features
- **Product Browsing & Search**
  - Advanced product filtering and search
  - Product variants (size, color, price)
  - Product ratings and reviews
  - Wishlist/favorites management

- **Shopping Experience**
  - Shopping cart management
  - Real-time inventory tracking
  - Multiple payment methods (VNPay integration)
  - Order tracking and history

- **AI-Powered Features**
  - Personalized product recommendations
  - AI-driven fashion trend analysis
  - Smart search and filtering

- **Communication**
  - Real-time chat with vendors
  - Push notifications
  - Order status updates

### Vendor Features
- **Store Management**
  - Product catalog management
  - Inventory control
  - Order processing
  - Sales analytics

- **Business Tools**
  - Advertisement management
  - Customer support system
  - Financial reporting
  - AI recommendation insights

### Admin Features
- **System Management**
  - User management
  - Product oversight
  - Order monitoring
  - Financial dashboard
  - Notification management

## 🛠️ Technology Stack

### Backend
- **Java 21** with Spring Boot 3.5.3
- **Spring Cloud Gateway** for API routing
- **Spring Data MongoDB** for data persistence
- **Spring Security** for authentication
- **Lombok** for code generation
- **Gradle** for build management

### Frontend
- **Vue.js 3.5.13** with Composition API
- **TypeScript 5.7.3** for type safety
- **Vite 6.1.0** for fast development
- **PrimeVue 4.2.5** for UI components
- **Tailwind CSS 4.0.9** for styling
- **Pinia 3.0.3** for state management
- **Vue Router 4.5.0** for navigation
- **Socket.IO Client 4.8.1** for real-time features
- **Axios 1.10.0** for HTTP requests

### Database
- **MongoDB** for product and user data
- **Document-based storage** for flexible schema

### External Integrations
- **VNPay** for payment processing
- **Giao Hang Nhanh (GHN)** for shipping
- **Socket.IO** for real-time communication
- **Ngrok** for development tunneling

## 📁 Project Structure

```
LVTN_Vu_Thu/
├── be/                          # Backend services
│   ├── api-gateway/            # Spring Cloud Gateway
│   ├── products-service/       # Product management service
│   └── build.gradle           # Root build configuration
├── fe/                         # Frontend application
│   ├── src/
│   │   ├── api/               # API integration layer
│   │   ├── components/        # Reusable Vue components
│   │   ├── layouts/           # Page layouts
│   │   ├── page/              # Page components
│   │   ├── router/            # Vue Router configuration
│   │   ├── store/             # Pinia state management
│   │   ├── types/             # TypeScript type definitions
│   │   └── composables/       # Vue composables
│   └── package.json
└── k8s/                       # Kubernetes deployment files
```

## 🚀 Getting Started

### Prerequisites
- **Java 21** (for backend services)
- **Node.js 18+** (for frontend)
- **MongoDB** (for database)
- **Gradle** (for backend builds)

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/tranthuyminhthu/LVTN_Vu_Thu.git
   cd LVTN_Vu_Thu
   ```

2. **Start MongoDB**
   ```bash
   # Make sure MongoDB is running on localhost:27017
   mongod
   ```

3. **Build and run backend services**
   ```bash
   # Build all services
   cd be
   ./gradlew build

   # Run API Gateway
   cd api-gateway
   ./gradlew bootRun

   # Run Products Service (in new terminal)
   cd ../products-service
   ./gradlew bootRun

   # Run Main Application (in new terminal)
   cd ..
   ./gradlew bootRun
   ```

### Frontend Setup

1. **Install dependencies**
   ```bash
   cd fe
   npm install
   ```

2. **Start development server**
   ```bash
   npm run dev
   ```

3. **Build for production**
   ```bash
   npm run build
   ```

### Environment Configuration

#### Backend Configuration
- **API Gateway**: `be/api-gateway/src/main/resources/application.yaml`
- **Products Service**: `be/products-service/src/main/resources/application.yaml`
- **Main Application**: `be/src/main/resources/application.properties`

#### Frontend Configuration
- **API Base URL**: Configure in `fe/src/api/index.ts`
- **Environment Variables**: Use `.env` files for different environments

## 🔧 Development

### Code Quality
- **ESLint** for JavaScript/TypeScript linting
- **Prettier** for code formatting
- **Oxlint** for additional linting
- **TypeScript** for type checking

### Available Scripts

#### Frontend
```bash
npm run dev          # Start development server
npm run build        # Build for production
npm run preview      # Preview production build
npm run lint         # Run all linters
npm run format       # Format code with Prettier
npm run type-check   # TypeScript type checking
```

#### Backend
```bash
./gradlew build      # Build all services
./gradlew test       # Run tests
./gradlew bootRun    # Run specific service
```

## 🌐 API Endpoints

### Products Service
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh access token

### Orders
- `GET /api/orders` - Get user orders
- `POST /api/orders` - Create new order
- `GET /api/orders/{id}` - Get order details

## 🔐 Authentication & Security

- **JWT-based authentication** with access and refresh tokens
- **Automatic token refresh** on frontend
- **Role-based access control** (Customer, Vendor, Admin)
- **Secure API endpoints** with proper authorization

## 📱 Real-time Features

- **WebSocket integration** for real-time chat
- **Live order updates** and notifications
- **Real-time inventory tracking**
- **Instant messaging** between customers and vendors

## 🚀 Deployment

### Docker Deployment
```bash
# Build Docker images
docker build -t fashion-ecommerce-frontend ./fe
docker build -t fashion-ecommerce-backend ./be

# Run with Docker Compose
docker-compose up -d
```

### Kubernetes Deployment
- Configuration files available in `k8s/` directory
- Supports horizontal scaling
- Load balancing and service discovery

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Frontend Development**: Vue.js, TypeScript, PrimeVue
- **Backend Development**: Spring Boot, Java, MongoDB
- **DevOps**: Docker, Kubernetes
- **AI Integration**: Recommendation systems, trend analysis

## 📞 Support

For support and questions, please contact the development team or create an issue in the repository.

---

**Note**: This is a comprehensive e-commerce solution designed for fashion retail with modern web technologies and AI integration capabilities.