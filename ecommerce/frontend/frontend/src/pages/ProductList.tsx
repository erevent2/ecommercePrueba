import { useState } from 'react';
import { useProducts } from '../hooks/useProducts';
import type { ProductFilters } from '../hooks/useProducts';
import ProductCard from '../components/ProductCard';
import { PRODUCT_CATEGORIES } from '../utils/constants';

export default function ProductList() {
  const [filters, setFilters] = useState<ProductFilters>({});
  
  // Consumo del backend con TanStack Query
  const { data: products, isLoading, isError } = useProducts(filters);

  // Manejador genérico para todos los inputs del filtro
  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFilters((prev) => ({
      ...prev,
      // Si el campo incluye 'Price', lo convertimos a número
      [name]: value ? (name.includes('Price') ? Number(value) : value) : undefined,
    }));
  };

  return (
    <div className="flex flex-col gap-8 lg:flex-row">
      {/* Barra lateral de Filtros */}
      <aside className="w-full shrink-0 space-y-6 lg:w-64">
        <div className="sticky top-8 rounded-xl bg-white p-6 shadow-sm ring-1 ring-blossom-900/5">
          <h2 className="mb-6 font-serif text-xl font-semibold text-blossom-900">
            Filtros
          </h2>
          
          <div className="space-y-5">
            <div>
              <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Buscar</label>
              <input
                type="text"
                name="name"
                onChange={handleFilterChange}
                placeholder="Ej. Teclado..."
                className="w-full rounded-lg border-0 bg-blossom-50 px-3 py-2.5 text-sm text-blossom-900 ring-1 ring-inset ring-blossom-900/10 focus:ring-2 focus:ring-inset focus:ring-blossom-accent"
              />
            </div>

            <div>
              <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Categoría</label>
              <select
                name="category"
                onChange={handleFilterChange}
                className="w-full rounded-lg border-0 bg-blossom-50 px-3 py-2.5 text-sm text-blossom-900 ring-1 ring-inset ring-blossom-900/10 focus:ring-2 focus:ring-inset focus:ring-blossom-accent"
              >
                <option value="">Todas las categorías</option>
                {PRODUCT_CATEGORIES.map((cat) => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Mínimo</label>
                <input
                  type="number"
                  name="minPrice"
                  min="0"
                  onChange={handleFilterChange}
                  placeholder="$ 0"
                  className="w-full rounded-lg border-0 bg-blossom-50 px-3 py-2.5 text-sm text-blossom-900 ring-1 ring-inset ring-blossom-900/10 focus:ring-2 focus:ring-inset focus:ring-blossom-accent"
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Máximo</label>
                <input
                  type="number"
                  name="maxPrice"
                  min="0"
                  onChange={handleFilterChange}
                  placeholder="$ 0"
                  className="w-full rounded-lg border-0 bg-blossom-50 px-3 py-2.5 text-sm text-blossom-900 ring-1 ring-inset ring-blossom-900/10 focus:ring-2 focus:ring-inset focus:ring-blossom-accent"
                />
              </div>
            </div>
          </div>
        </div>
      </aside>

      {/* Cuadrícula de Resultados */}
      <section className="flex-1">
        {isLoading && (
          <div className="flex h-64 w-full items-center justify-center rounded-xl bg-white/50">
            <div className="loader-spinner"></div>
          </div>
        )}
        
        {isError && (
          <div className="flex h-64 w-full flex-col items-center justify-center rounded-xl bg-red-50 p-6 text-center text-red-800 ring-1 ring-red-100">
            <p className="font-medium">Ocurrió un error al cargar el catálogo.</p>
            <p className="mt-1 text-sm text-red-600">Por favor, verifica tu conexión al servidor.</p>
          </div>
        )}
        
        {!isLoading && !isError && products?.length === 0 && (
          <div className="flex h-64 w-full flex-col items-center justify-center rounded-xl border border-dashed border-blossom-900/20 bg-white/50 text-blossom-900/60">
            <p>No encontramos productos que coincidan con tu búsqueda.</p>
          </div>
        )}

        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-3">
          {products?.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </section>
    </div>
  );
}