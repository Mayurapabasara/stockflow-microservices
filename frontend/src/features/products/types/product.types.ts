export type ProductStatus = 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED'

export interface Category {
  id: string
  name: string
  description?: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export type Brand = Category

export interface Product {
  id: string
  sku: string
  name: string
  description?: string
  price: number
  status: ProductStatus
  category: Category
  brand: Brand
  createdAt: string
  updatedAt: string
  version: number
}

export interface ProductInput {
  sku: string
  name: string
  description?: string
  price: number
  status: ProductStatus
  categoryId: string
  brandId: string
}

export interface CatalogInput {
  name: string
  description?: string
  active: boolean
}
