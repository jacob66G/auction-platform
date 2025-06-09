export interface Category {
  id?: number;
  name: string;
}

export interface CategoryRequest {
  name: string;
}

export interface CategoryResponse {
  id: number;
  name: string;
} 