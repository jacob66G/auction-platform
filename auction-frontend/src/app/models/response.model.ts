/**
 * Model dla prostych odpowiedzi z wiadomością
 */
export interface MessageResponse {
  message: string;
}

/**
 * Model dla odpowiedzi z błędem
 */
export interface ErrorResponse {
  status: number;
  message: string;
  details: string;
  timestamp: string;
} 