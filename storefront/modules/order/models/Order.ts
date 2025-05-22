import { OrderItem } from './OrderItem';
import { Address } from '@/modules/address/models/AddressModel';
export type Order = {
  id?: number;
  email: string;
  note?: string;
  tax?: number;
  discount?: number;
  numberItem: number;
  totalPrice: number;
  deliveryFee?: number | 0;
  couponCode?: string | '';
  deliveryMethod: string;
  deliveryStatus?: string;
  paymentMethod: string;
  paymentStatus: string;
  orderItemPostVms: OrderItem[];
  shippingAddressPostVm: Address;
  billingAddressPostVm: Address;

  checkoutId?: string;
};
