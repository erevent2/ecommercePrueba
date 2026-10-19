import { useQuery } from '@tanstack/react-query';
import { api } from '../api/axiosConfig';
import type { Product } from '../types/Product';

export interface ProductFilters {
  name?: string;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
}

export const useProducts = (filters: ProductFilters) => {
  return useQuery({
    // La queryKey actúa como el identificador de la caché.
    // Si 'filters' cambia, React Query sabe que debe buscar nueva data.
    queryKey: ['products', filters],
    queryFn: async () => {
      // Limpiamos los filtros indefinidos para no enviar parámetros vacíos en la URL
      const params = Object.fromEntries(
        Object.entries(filters).filter(([_, value]) => value !== undefined && value !== '')
      );

      const { data } = await api.get<Product[]>('/products', { params });
      return data;
    },
  });
};