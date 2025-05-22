import { ProductSearchSuggestions } from '../models/ProductSearchSuggestions';
import { SearchParams } from '../models/SearchParams';
import { SearchProductResponse } from '../models/SearchProductResponse';
import apiClientService from '@/common/services/ApiClientService';

export async function getSuggestions(keyword: string): Promise<ProductSearchSuggestions> {
  const response = await apiClientService.get(
    `/api/search/storefront/search_suggest?keyword=${keyword}`
  );
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }

  throw new Error(response.statusText);
}

export async function searchProducts(params: SearchParams): Promise<SearchProductResponse> {
  let url = `api/search/storefront/catalog-search?keyword=${params.keyword}`;
  if (params.category) {
    url += `&category=${params.category}`;
  }
  if (params.brand) {
    url += `&brand=${params.brand}`;
  }
  if (params.attribute) {
    url += `&attribute=${params.attribute}`;
  }
  if (params.minPrice) {
    url += `&minPrice=${params.minPrice}`;
  }
  if (params.maxPrice) {
    url += `&maxPrice=${params.maxPrice}`;
  }
  if (params.sortType) {
    url += `&sortType=${params.sortType}`;
  }
  if (params.page) {
    url += `&page=${params.page}`;
  }
  if (params.pageSize) {
    url += `&pageSize=${params.pageSize}`;
  }
  const response = await apiClientService.get(url);
  if (response.status >= 200 && response.status < 300) {
    return await response.json();
  }
  throw new Error(response.statusText);
}
