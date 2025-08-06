import type { CalculateFeePayload, GetServicePayload } from "@/types";
import axios from "axios";

const ghnApi = axios.create({
  baseURL: "https://dev-online-gateway.ghn.vn/shiip/public-api",
  headers: {
    "Content-Type": "application/json",
    "Token": import.meta.env.VITE_GHN_TOKEN,
  },
});

export const getProvince = async () => {
  const response = await ghnApi.get("/master-data/province");
  return response.data;
};

export const getProvinceNew = async () => {
    const response = await ghnApi.get("/v3/master-data/province/all");
    return response.data;
  };

export const getDistrict = async (provinceId: number) => {
  const response = await ghnApi.post(`/master-data/district`, {
    province_id: provinceId,
  });
  return response.data;
};

export const getWard = async (districtId: number) => {
  const response = await ghnApi.get(`/master-data/ward?district_id=${districtId}`);
  return response.data;
};

export const getWardNew = async (provinceId: number) => {
    const response = await ghnApi.post(`/v3/master-data/ward/all-by-province-id`, {
        province_id: provinceId,
    });
    return response.data;
  };

export const calculateFee = async (data: CalculateFeePayload) => {
  const response = await ghnApi.post(`/v2/shipping-order/fee`, data);
  return response.data;
};

export const getService = async (data: GetServicePayload) => {
  const response = await ghnApi.post(`/v2/shipping-order/available-services`, data);
  return response.data;
};

export default ghnApi;