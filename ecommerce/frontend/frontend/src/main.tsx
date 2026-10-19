import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import App from './App.tsx';
import './index.css';

// Instanciamos el cliente de React Query
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false, // Evita peticiones innecesarias al cambiar de pestaña
      retry: 1, // Reintenta 1 vez si falla la petición
      staleTime: 1000 * 60 * 5, // La data se considera "fresca" por 5 minutos
    },
  },
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>

    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
    
  </StrictMode>,
);