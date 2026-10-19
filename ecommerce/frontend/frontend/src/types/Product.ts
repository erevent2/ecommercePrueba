export interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  productType: 'PHYSICAL' | 'DIGITAL';
  // Extras para UI:
  images?: string[]; 
  weightKg?: number;
  downloadLink?: string;
}

