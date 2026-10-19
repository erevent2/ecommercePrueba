import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/axiosConfig';
import type { Product } from '../types/Product';
import { PRODUCT_CATEGORIES } from '../utils/constants';

export default function ProductForm() {
  const navigate = useNavigate();
  const { register, handleSubmit, watch, formState: { errors, isSubmitting } } = useForm<Partial<Product>>();
  
  // Observamos el valor del tipo de producto para mostrar los campos correctos
  const productType = watch('productType');

  const onSubmit = async (data: Partial<Product>) => {
    try {
      await api.post('/products', data);
      alert('Producto creado exitosamente');
      navigate('/');
    } catch (error) {
      alert('Error al crear el producto. Revisa tu sesión o permisos.');
    }
  };

  return (
    <div className="mx-auto max-w-2xl rounded-2xl bg-white p-8 shadow-sm ring-1 ring-blossom-900/5">
      <h2 className="mb-6 font-serif text-3xl font-semibold text-blossom-900">Crear Nuevo Producto</h2>
      
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
          {/* Nombre */}
          <div className="col-span-2">
            <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Nombre del Producto</label>
            <input
              {...register('name', { required: 'El nombre es obligatorio' })}
              className="w-full rounded-lg bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-blossom-900/10 focus:ring-2 focus:ring-blossom-accent"
            />
            {errors.name && <span className="mt-1 text-xs text-red-500">{errors.name.message}</span>}
          </div>

          {/* Categoría */}
          <div>
            <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Categoría</label>
            <select
              {...register('category', { required: 'La categoría es obligatoria' })}
              className="w-full rounded-lg bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-blossom-900/10 focus:ring-2 focus:ring-blossom-accent"
            >
              <option value="">Selecciona una categoría...</option>
              {PRODUCT_CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
            {errors.category && <span className="mt-1 text-xs text-red-500">{errors.category.message}</span>}
          </div>

          {/* Precio */}
          <div>
            <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Precio ($)</label>
            <input
              type="number"
              step="0.01"
              {...register('price', { required: 'El precio es obligatorio', min: { value: 0.1, message: 'Debe ser mayor a 0' } })}
              className="w-full rounded-lg bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-blossom-900/10 focus:ring-2 focus:ring-blossom-accent"
            />
            {errors.price && <span className="mt-1 text-xs text-red-500">{errors.price.message}</span>}
          </div>

          {/* Tipo de Producto */}
          <div className="col-span-2">
            <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Tipo de Producto</label>
            <select
              {...register('productType', { required: 'Selecciona un tipo' })}
              className="w-full rounded-lg bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-blossom-900/10 focus:ring-2 focus:ring-blossom-accent"
            >
              <option value="">Selecciona...</option>
              <option value="PHYSICAL">Producto Físico</option>
              <option value="DIGITAL">Producto Digital</option>
            </select>
          </div>

          {/* Campos Dinámicos: FÍSICO */}
          {productType === 'PHYSICAL' && (
            <>
              <div>
                <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Peso (Kg)</label>
                <input
                  type="number"
                  step="0.1"
                  {...register('weightKg', { required: 'El peso es obligatorio para envíos' })}
                  className="w-full rounded-lg bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-blossom-900/10 focus:ring-2 focus:ring-blossom-accent"
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Dimensiones (ej. 10x20x5)</label>
                <input
                  {...register('downloadLink')} // Reusamos el tipo dinámico
                  className="w-full rounded-lg bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-blossom-900/10 focus:ring-2 focus:ring-blossom-accent"
                />
              </div>
            </>
          )}

          {/* Campos Dinámicos: DIGITAL */}
          {productType === 'DIGITAL' && (
            <>
              <div className="col-span-2">
                <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Enlace de Descarga</label>
                <input
                  {...register('downloadLink', { required: 'El enlace es obligatorio' })}
                  className="w-full rounded-lg bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-blossom-900/10 focus:ring-2 focus:ring-blossom-accent"
                />
              </div>
            </>
          )}
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          className="mt-6 w-full rounded-lg bg-blossom-900 px-4 py-3 font-medium text-white transition-colors hover:bg-blossom-accent"
        >
          {isSubmitting ? 'Guardando...' : 'Crear Producto'}
        </button>
      </form>
    </div>
  );
}