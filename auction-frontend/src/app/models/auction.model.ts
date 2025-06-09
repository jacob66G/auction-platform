import { Category } from './category.model';
import { User } from './user.model';
import { Bid } from './bid.model';

export enum AuctionStatus {
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  REJECTED = 'REJECTED',
  ACTIVE = 'ACTIVE',
  FINISHED = 'FINISHED',
  EXPIRED = 'EXPIRED',
  CANCELLED = 'CANCELLED'
}

export interface Auction {
  id?: number;
  title: string;
  description: string;
  startingPrice: number;
  startTime: Date | string;
  endTime: Date | string;
  auctionStatus?: AuctionStatus;
  actualPrice?: number;
  user?: User;
  category?: Category;
  auctionImgs?: AuctionImg[];
  bids?: Bid[];
}

export interface AuctionImg {
  id?: number;
  url: string;
}

export interface AuctionResponse {
  id: number;
  title: string;
  endTime: string;
  username: string;
  categoryName: string;
  auctionImgUrl: string;
  actualPrice: number;
  auctionStatus?: string;
}

export interface AuctionDetailsResponse {
  id: number;
  title: string;
  description: string;
  startingPrice: number;
  startTime: string;
  endTime: string;
  auctionStatus: string;
  username: string;
  categoryName: string;
  auctionImgsUrls: string[];
  actualPrice: number;
  bids: Bid[];
  cancelRequestPending?: boolean;
  cancelRequestCount?: number;
}

export interface AuctionCreateResponse {
  id: number;
  title: string;
  message: string;
}

export interface AuctionCreateDto {
  title: string;
  description: string;
  startingPrice: number;
  startTime: string;
  endTime: string;
  categoryId: number;
}

export interface AuctionSearchCriteria {
  title?: string;
  username?: string;
  categoryIds?: number[];
  statuses?: string[];
  sortBy?: string;
  ascending?: boolean;
  page?: number;
  size?: number;
}

export interface AuctionCancelRequest {
  reason: string;
} 