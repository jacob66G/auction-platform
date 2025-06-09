import { User } from './user.model';
import { Auction } from './auction.model';
import { BidResponse } from './bid.model';

export enum RequestStatus {
  PENDING = 'PENDING',
  APPROVE = 'APPROVE',
  REJECT = 'REJECT'
}

export enum RequestType {
  SAVE = 'SAVE',
  EDIT = 'EDIT',
  CANCEL = 'CANCEL'
}

export interface AuctionRequest {
  id?: number;
  auction?: Auction;
  requestType: RequestType;
  requestStatus?: RequestStatus;
  userReason?: string;
  moderatorComment?: string;
  requestDate?: Date | string;
  decisionDate?: Date | string;
  requestedBy?: User;
  moderatedBy?: User;
}

export interface AuctionRequestResponse {
  id: number;
  auctionId: number;
  auctionTitle: string;
  requestType: string;
  requestStatus: string;
  reason: string;
  comment: string;
  requestDate: string;
  decisionDate: string;
  requesterName: string;
  moderatorName: string;
}

export interface AuctionRequestDetailsResponse {
  id: number;
  requestType: string;
  requestStatus: string;
  reason: string;
  comment: string;
  requestDate: string;
  decisionDate: string;
  requesterName: string;
  moderatorName: string;
  auctionId: number;
  auctionTitle: string;
  description: string;
  startingPrice: number;
  startTime: string;
  endTime: string;
  auctionStatus: string;
  username: string;
  categoryName: string;
  auctionImgsUrls: string[];
  actualPrice: number;
  bids: BidResponse[];
}

export interface AuctionRequestComment {
  comment: string;
}

export interface AuctionRequestCriteria {
  auctionTitle?: string;
  username?: string;
  requestTypes?: string[];
  requestStatuses?: string[];
  sortBy?: string;
  ascending?: boolean;
  page?: number;
  size?: number;
} 