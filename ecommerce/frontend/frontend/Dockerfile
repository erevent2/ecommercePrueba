# Etapa 1: Construcción (Build)
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Etapa 2: Servidor Web (NGINX)
FROM nginx:alpine
# Copiamos la carpeta 'dist' (resultado del build de Vite) a la carpeta de Nginx
COPY --from=builder /app/dist /usr/share/nginx/html
# Exponemos el puerto 80 (puerto por defecto de Nginx)
EXPOSE 80
# Iniciamos Nginx
CMD ["nginx", "-g", "daemon off;"]