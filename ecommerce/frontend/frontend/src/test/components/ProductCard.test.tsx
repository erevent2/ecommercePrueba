import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ProductCard from '../../components/ProductCard';
import * as CartContextModule from '../../context/CartContext';
import type { Product } from '../../types/Product';

// Hacemos un mock (simulación) del hook del carrito
const mockAddToCart = vi.fn();
vi.spyOn(CartContextModule, 'useCart').mockReturnValue({
  cart: [],
  addToCart: mockAddToCart,
  removeFromCart: vi.fn(),
  clearCart: vi.fn(),
  totalAmount: 0,
});

const mockProduct: Product = {
  id: 1,
  name: 'Teclado Mecánico',
  category: 'Electrónica',
  price: 120.50,
  productType: 'PHYSICAL',
};

describe('ProductCard Component', () => {
  it('debe renderizar el nombre, categoría y precio del producto', () => {
    render(<ProductCard product={mockProduct} />);

    expect(screen.getByText('Teclado Mecánico')).toBeTruthy();
    expect(screen.getByText('Electrónica')).toBeTruthy();
    expect(screen.getByText('$120.50')).toBeTruthy();
  });

  it('debe llamar a addToCart con los datos correctos al hacer clic en Agregar', () => {
    render(<ProductCard product={mockProduct} />);
    
    const addButton = screen.getByRole('button', { name: /agregar/i });
    fireEvent.click(addButton);

    expect(mockAddToCart).toHaveBeenCalledTimes(1);
    expect(mockAddToCart).toHaveBeenCalledWith({
      productId: 1,
      name: 'Teclado Mecánico',
      price: 120.50,
    });
  });

  it('debe usar la imagen del producto y ocultar el placeholder de carga al terminar', () => {
    render(<ProductCard product={{ ...mockProduct, images: ['https://example.test/product.png'] }} />);

    const image = screen.getByRole('img', { name: 'Teclado Mecánico' });
    expect(image).toHaveAttribute('src', 'https://example.test/product.png');
    fireEvent.load(image);

    expect(image).toHaveClass('opacity-100');
    expect(document.querySelector('.loader-spinner')).not.toBeInTheDocument();
  });
});