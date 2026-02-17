import axiosInstance from "./axiosInstance";

export const fetchGroupPool = async (groupId) => {
  const res = await axiosInstance.get(
    `/groups/${groupId}/pool`
  );
  return res.data;
};

export const fetchPoolTransactions = async (
  groupId
) => {
  const res = await axiosInstance.get(
    `/groups/${groupId}/pool/transactions`
  );
  return res.data;
};

export const depositToPool = (
  groupId,
  payload
) => {
  return axiosInstance.post(
    `/groups/${groupId}/pool/deposit`,
    payload
  );
};

export const withdrawFromPool = (
  groupId,
  payload
) => {
  return axiosInstance.post(
    `/groups/${groupId}/pool/withdraw`,
    payload
  );
};
export const settlePool = (groupId) => {
  return axiosInstance.post(
    `/groups/${groupId}/pool/settle`
  );
};
