import { useQuery } from '@tanstack/react-query'
import { productApi } from '../api/productApi'

export function useProducts(page: number) {
  return useQuery({
    queryKey: ['products', page],
    queryFn: () => productApi.list(page),
  })
}

export function useCatalogOptions() {
  const categories = useQuery({
    queryKey: ['categories'],
    queryFn: productApi.categories,
  })
  const brands = useQuery({
    queryKey: ['brands'],
    queryFn: productApi.brands,
  })
  return { categories, brands }
}
