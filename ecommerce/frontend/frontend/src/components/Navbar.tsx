import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext'; // 1. Importa el hook

export default function Navbar() {
  const navigate = useNavigate();
  const { cart } = useCart(); // 2. Extrae el carrito
  const isAuthenticated = !!localStorage.getItem('blossom_token');

  // 3. Calcula la cantidad total de ítems
  const cartItemCount = cart.reduce((total, item) => total + item.quantity, 0);

  const handleLogout = () => {
    localStorage.removeItem('blossom_token');
    navigate('/');
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b border-blossom-900/10 bg-blossom-50/80 backdrop-blur-md">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4">
        <Link to="/" className="flex items-center gap-2">
          <span className="font-serif text-2xl font-bold tracking-tight text-blossom-900">
            Blossom.
          </span>
        </Link>

        <nav className="flex items-center gap-6">
          <Link to="/" className="text-sm font-medium text-blossom-900/80 transition-colors hover:text-blossom-accent">
            Catálogo
          </Link>
          
          {isAuthenticated ? (
            <>
              <Link to="/admin/products/new" className="text-sm font-medium text-blossom-900/80 transition-colors hover:text-blossom-accent">
                Crear Producto
              </Link>
              
              {/* 4. Actualizamos el enlace del carrito con relative y la burbuja */}
              <Link to="/account" className="relative text-sm font-medium text-blossom-900/80 transition-colors hover:text-blossom-accent">
                Mi Cuenta & Carrito
                {cartItemCount > 0 && (
                  <span className="absolute -right-3 -top-2 flex h-4 w-4 items-center justify-center rounded-full bg-blossom-accent text-[10px] font-bold text-white">
                    {cartItemCount}
                  </span>
                )}
              </Link>
              
              <button 
                onClick={handleLogout}
                className="rounded-full bg-blossom-900 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-blossom-accent"
              >
                Cerrar Sesión
              </button>
            </>
          ) : (
            <Link 
              to="/auth" 
              className="rounded-full bg-blossom-900 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-blossom-accent"
            >
              Iniciar Sesión
            </Link>
          )}
        </nav>
      </div>
    </header>
  );
}