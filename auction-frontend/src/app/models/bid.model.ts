export interface Bid {
  id: number;
  amount: number;
  bidTime: string;
  username: string;
}

export interface BidRequest {
  amount: number;
  auctionId: number;
}

export interface BidResponse {
  id: number;
  amount: number;
  bidDate: string;
  username: string;
  auctionId?: number;
  auctionTitle?: string;
  isWinning?: boolean;
  auctionEnded?: boolean;
} 