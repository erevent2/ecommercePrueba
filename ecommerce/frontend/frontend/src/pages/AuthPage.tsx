import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/axiosConfig';

interface AuthFormInputs {
  email: string;
  password: string;
}

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [serverError, setServerError] = useState('');
  const navigate = useNavigate();
  
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<AuthFormInputs>();

  const onSubmit = async (data: AuthFormInputs) => {
    setServerError('');
    try {
      if (isLogin) {
        const response = await api.post('/auth/login', data);
        localStorage.setItem('blossom_token', response.data.token);
        navigate('/'); // Redirige al catálogo al iniciar sesión exitosamente
      } else {
        await api.post('/auth/register', data);
        setIsLogin(true); // Si el registro es exitoso, lo pasamos al login
        alert('Cuenta creada exitosamente. Por favor inicia sesión.');
      }
    } catch (error: any) {
      setServerError(error.response?.data?.message || 'Ocurrió un error en el servidor. Intenta de nuevo.');
    }
  };

  return (
    <div className="mx-auto mt-16 max-w-md overflow-hidden rounded-2xl bg-white shadow-sm ring-1 ring-blossom-900/5">
      <div className="p-8">
        <h2 className="mb-2 font-serif text-3xl font-semibold text-blossom-900">
          {isLogin ? 'Bienvenido de vuelta' : 'Crea tu cuenta'}
        </h2>
        <p className="mb-8 text-sm text-blossom-900/60">
          {isLogin ? 'Ingresa tus credenciales para continuar.' : 'Únete a Blossom para realizar tus compras.'}
        </p>

        {serverError && (
          <div className="mb-6 rounded-lg bg-red-50 p-4 text-sm text-red-800 ring-1 ring-red-100">
            {serverError}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div>
            <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Correo electrónico</label>
            <input
              type="email"
              {...register('email', { required: 'El correo es obligatorio' })}
              className="w-full rounded-lg border-0 bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-inset ring-blossom-900/10 focus:ring-2 focus:ring-inset focus:ring-blossom-accent"
              placeholder="tu@email.com"
            />
            {errors.email && <span className="mt-1 block text-xs text-red-500">{errors.email.message}</span>}
          </div>

          <div>
            <label className="mb-1.5 block text-sm font-medium text-blossom-900/80">Contraseña</label>
            <input
              type="password"
              {...register('password', { required: 'La contraseña es obligatoria', minLength: { value: 6, message: 'Mínimo 6 caracteres' } })}
              className="w-full rounded-lg border-0 bg-blossom-50 px-4 py-3 text-blossom-900 ring-1 ring-inset ring-blossom-900/10 focus:ring-2 focus:ring-inset focus:ring-blossom-accent"
              placeholder="••••••••"
            />
            {errors.password && <span className="mt-1 block text-xs text-red-500">{errors.password.message}</span>}
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="mt-4 flex w-full items-center justify-center rounded-lg bg-blossom-accent px-4 py-3 text-sm font-medium text-white transition-colors hover:bg-[#c26547] disabled:opacity-70"
          >
            {isSubmitting ? (
              <div className="loader-spinner border-l-white h-5 w-5 border-2"></div>
            ) : (
              isLogin ? 'Iniciar Sesión' : 'Registrarse'
            )}
          </button>
        </form>

        <div className="mt-6 text-center text-sm text-blossom-900/60">
          {isLogin ? '¿No tienes cuenta? ' : '¿Ya tienes cuenta? '}
          <button 
            type="button" 
            onClick={() => setIsLogin(!isLogin)} 
            className="font-medium text-blossom-accent hover:underline"
          >
            {isLogin ? 'Regístrate aquí' : 'Inicia sesión'}
          </button>
        </div>
      </div>
    </div>
  );
}