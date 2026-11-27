import { api } from '../../../api/axiosInstance'
import { ENDPOINTS } from '../../../constants/endpoints'
import type { PageResponse } from '../../../types/api.types'
import type {
  Brand,
  CatalogInput,
  Category,
  Product,
  ProductInput,
} from '../types/product.types'

export const productApi = {
  list: async (page = 0, size = 10) =>
    (
      await api.get<PageResponse<Product>>(ENDPOINTS.products, {
        params: { page, size, sort: 'name,asc' },
      })
    ).data,
  get: async (id: string) =>
    (await api.get<Product>(`${ENDPOINTS.products}/${id}`)).data,
  create: async (input: ProductInput) =>
    (await api.post<Product>(ENDPOINTS.products, input)).data,
  update: async (id: string, input: ProductInput) =>
    (await api.put<Product>(`${ENDPOINTS.products}/${id}`, input)).data,
  remove: async (id: string) =>
    api.delete(`${ENDPOINTS.products}/${id}`),
  categories: async () =>
    (await api.get<Category[]>(ENDPOINTS.categories)).data,
  brands: async () => (await api.get<Brand[]>(ENDPOINTS.brands)).data,
  createCategory: async (input: CatalogInput) =>
    (await api.post<Category>(ENDPOINTS.categories, input)).data,
  createBrand: async (input: CatalogInput) =>
    (await api.post<Brand>(ENDPOINTS.brands, input)).data,
  removeCategory: async (id: string) =>
    api.delete(`${ENDPOINTS.categories}/${id}`),
  removeBrand: async (id: string) =>
    api.delete(`${ENDPOINTS.brands}/${id}`),
}
