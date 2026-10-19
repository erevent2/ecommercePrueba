import { useState } from 'react';
import type { Product } from '../types/Product';
import { useCart } from '../context/CartContext';

interface ProductCardProps {
  product: Product;
}

export default function ProductCard({ product }: ProductCardProps) {
  const [imageLoaded, setImageLoaded] = useState(false);
  const { addToCart } = useCart(); // Extraemos la función para agregar al carrito
  
  // Utilizamos un placeholder si el backend aún no envía la URL real de la imagen
  const imageUrl = product.images?.[0] || `https://placehold.co/400x400/f2e8e5/2c1c19?text=${encodeURIComponent(product.name)}`;

  const handleAddToCart = () => {
    addToCart({
      productId: product.id,
      name: product.name,
      price: product.price,
    });
  };

  return (
    <div className="group flex flex-col overflow-hidden rounded-xl bg-white shadow-sm ring-1 ring-blossom-900/5 transition-all hover:shadow-md hover:ring-blossom-900/10">
      <div className="relative aspect-square overflow-hidden bg-blossom-50">
        {!imageLoaded && (
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="loader-spinner"></div>
          </div>
        )}
        <img
          src={imageUrl}
          alt={product.name}
          loading="lazy"
          onLoad={() => setImageLoaded(true)}
          className={`h-full w-full object-cover transition-all duration-500 ${
            imageLoaded ? 'opacity-100 scale-100' : 'opacity-0 scale-105'
          } group-hover:scale-105`}
        />
        <span className="absolute left-3 top-3 rounded-full bg-blossom-900/90 px-3 py-1 text-xs font-medium tracking-wide text-white backdrop-blur-sm">
          {product.category}
        </span>
      </div>
      
      <div className="flex flex-1 flex-col p-5">
        <h3 className="font-serif text-lg font-semibold text-blossom-900 line-clamp-1">
          {product.name}
        </h3>
        <p className="mt-1 text-sm text-blossom-900/60">
          {product.productType === 'PHYSICAL' ? 'Envío físico' : 'Descarga digital'}
        </p>
        
        <div className="mt-auto flex items-center justify-between pt-4">
          <span className="text-xl font-bold text-blossom-accent">
            ${product.price.toFixed(2)}
          </span>
          <button 
            onClick={handleAddToCart}
            className="rounded-full bg-blossom-100 px-5 py-2 text-sm font-medium text-blossom-900 transition-colors hover:bg-blossom-accent hover:text-white active:scale-95"
          >
            Agregar
          </button>
        </div>
      </div>
    </div>
  );
}