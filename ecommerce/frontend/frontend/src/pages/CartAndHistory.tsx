import { useQuery } from '@tanstack/react-query';
import { useCart } from '../context/CartContext';
import { api } from '../api/axiosConfig';

interface OrderHistoryItem {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
}

interface OrderHistoryEntry {
  id: number;
  status: string;
  items: OrderHistoryItem[];
}

export default function CartAndHistory() {
  const { cart, removeFromCart, clearCart, totalAmount } = useCart();
  const authToken = localStorage.getItem('blossom_token');

  // Obtenemos el historial de órdenes del backend
  const { data: history, isLoading, isError, refetch } = useQuery({
    queryKey: ['orderHistory', authToken],
    enabled: Boolean(authToken),
    staleTime: 0,
    refetchOnMount: 'always',
    queryFn: async () => {
      const { data } = await api.get<OrderHistoryEntry[]>('/orders');
      return data;
    },
  });

  const handleCheckout = async () => {
    if (cart.length === 0) return;
    
    try {
      const payload = {
        items: cart.map(item => ({ productId: item.productId, quantity: item.quantity }))
      };
      await api.post('/orders', payload);
      alert('¡Orden creada exitosamente!');
      clearCart();
      refetch(); // Refrescamos el historial automáticamente
    } catch {
      alert('Error al procesar la orden.');
    }
  };

  return (
    <div className="grid grid-cols-1 gap-12 lg:grid-cols-2">
      {/* SECCIÓN DEL CARRITO */}
      <section className="rounded-2xl bg-white p-8 shadow-sm ring-1 ring-blossom-900/5">
        <h2 className="mb-6 font-serif text-2xl font-semibold text-blossom-900">Tu Carrito</h2>
        
        {cart.length === 0 ? (
          <p className="text-blossom-900/60">El carrito está vacío.</p>
        ) : (
          <div className="space-y-4">
            {cart.map((item) => (
              <div key={item.productId} className="flex items-center justify-between border-b border-blossom-900/5 pb-4">
                <div>
                  <h4 className="font-medium text-blossom-900">{item.name}</h4>
                  <p className="text-sm text-blossom-900/60">Cantidad: {item.quantity}</p>
                </div>
                <div className="flex items-center gap-4">
                  <span className="font-semibold text-blossom-accent">${(item.price * item.quantity).toFixed(2)}</span>
                  <button 
                    onClick={() => removeFromCart(item.productId)}
                    className="text-xs font-medium text-red-500 hover:underline"
                  >
                    Eliminar
                  </button>
                </div>
              </div>
            ))}
            
            <div className="flex items-center justify-between pt-4">
              <span className="text-lg font-bold text-blossom-900">Total:</span>
              <span className="text-2xl font-bold text-blossom-accent">${totalAmount.toFixed(2)}</span>
            </div>
            
            <button
              onClick={handleCheckout}
              className="mt-6 w-full rounded-lg bg-blossom-accent px-4 py-3 font-medium text-white transition-colors hover:bg-[#c26547]"
            >
              Completar Compra
            </button>
          </div>
        )}
      </section>

      {/* SECCIÓN DEL HISTORIAL */}
      <section className="rounded-2xl bg-white p-8 shadow-sm ring-1 ring-blossom-900/5">
        <h2 className="mb-6 font-serif text-2xl font-semibold text-blossom-900">Historial de Órdenes</h2>
        
        {!authToken ? (
          <p className="text-blossom-900/60">Inicia sesión para consultar tu historial de órdenes.</p>
        ) : isLoading ? (
          <div className="loader-spinner"></div>
        ) : isError ? (
          <p role="alert" className="text-red-600">No se pudo cargar el historial de órdenes. Inténtalo de nuevo.</p>
        ) : !history || history.length === 0 ? (
          <p className="text-blossom-900/60">Aún no has realizado ninguna compra.</p>
        ) : (
          <div className="space-y-6">
            {history.map((order) => (
              <div key={order.id} className="rounded-xl bg-blossom-50 p-5 ring-1 ring-blossom-900/10">
                <div className="mb-3 flex items-center justify-between border-b border-blossom-900/10 pb-3">
                  <span className="text-sm font-medium text-blossom-900/60">Orden #{order.id}</span>
                  <span className="rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-semibold text-green-800">
                    {order.status}
                  </span>
                </div>
                <div className="space-y-2">
                  {order.items.map((item) => (
                    <div key={`${order.id}-${item.productId}`} className="flex justify-between text-sm">
                      <span className="text-blossom-900">{item.quantity}x {item.productName}</span>
                      <span className="font-medium text-blossom-900">${item.unitPrice.toFixed(2)}</span>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}