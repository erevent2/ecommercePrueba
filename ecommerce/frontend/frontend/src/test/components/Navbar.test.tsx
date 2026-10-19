import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { CartProvider } from '../../context/CartContext';

// Helper para renderizar componentes que usan React Router
const renderWithRouter = (ui: React.ReactElement) => {
  return render(
    <CartProvider>
      <BrowserRouter>{ui}</BrowserRouter>
    </CartProvider>,
  );
};

describe('Navbar Component', () => {
  beforeEach(() => {
    localStorage.clear(); // Limpiamos el storage antes de cada prueba
  });

  it('debe mostrar el botón de Iniciar Sesión cuando el usuario no está autenticado', () => {
    renderWithRouter(<Navbar />);
    
    expect(screen.getByText(/iniciar sesión/i)).toBeTruthy();
    expect(screen.queryByText(/cerrar sesión/i)).toBeNull();
  });

  it('debe mostrar opciones protegidas y botón de Cerrar Sesión cuando hay un token', () => {
    localStorage.setItem('blossom_token', 'token-falso-123');
    renderWithRouter(<Navbar />);
    
    expect(screen.getByText(/mi cuenta & carrito/i)).toBeTruthy();
    expect(screen.getByText(/crear producto/i)).toBeTruthy();
    expect(screen.getByText(/cerrar sesión/i)).toBeTruthy();
  });

  it('debe limpiar el token al hacer clic en Cerrar Sesión', () => {
    localStorage.setItem('blossom_token', 'token-falso-123');
    renderWithRouter(<Navbar />);
    
    const logoutButton = screen.getByText(/cerrar sesión/i);
    fireEvent.click(logoutButton);
    
    expect(localStorage.getItem('blossom_token')).toBeNull();
  });
});