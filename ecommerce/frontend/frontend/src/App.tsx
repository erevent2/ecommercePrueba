import { lazy, Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// Componentes estáticos críticos
import Navbar from './components/Navbar.tsx';
import './index.css'; // Estilos globales de la aplicación
import { CartProvider } from './context/CartContext.tsx';

// Pantallas cargadas perezosamente (Lazy Loading)
const ProductList = lazy(() => import('./pages/ProductList.tsx'));
const ProductForm = lazy(() => import('./pages/ProductForm'));
const AuthPage = lazy(() => import('./pages/AuthPage')); // Login y Registro
const CartAndHistory = lazy(() => import('./pages/CartAndHistory'));

// Componente de carga minimalista
const PageLoader = () => (
  <div className="flex h-screen w-full items-center justify-center bg-blossom-50">
    <div className="loader-spinner"></div>
  </div>
);

function App() {
  return (

    <Router>
      <CartProvider>
        <Navbar />
        <main className="min-h-screen px-4 py-8 max-w-7xl mx-auto">
          <Suspense fallback={<PageLoader />}>
            <Routes>
              <Route path="/" element={<ProductList />} />
              <Route path="/auth" element={<AuthPage />} />
              <Route path="/admin/products/new" element={<ProductForm />} />
              <Route path="/account" element={<CartAndHistory />} />
            </Routes>
          </Suspense>
        </main>
      </CartProvider>
    </Router>
  );
}

export default App;