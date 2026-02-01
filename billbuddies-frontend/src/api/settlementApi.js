import axiosInstance from "./axiosInstance";

// GET settlement rows
export const fetchGroupSettlement = async (groupId) => {
  const res = await axiosInstance.get(
    `/groups/${groupId}/settle`
  );
  return res.data; // returns OBJECT { groupId, generatedAt, settlements }
};

// Add settlement expense (row-level)
export const addSettlementExpense = async (groupId, payload) => {
  return axiosInstance.post(
    `/groups/${groupId}/expenses`,
    payload
  );
};

// Execute settlement (global or row follow-up)
export const executeSettlement = async (groupId) => {
  return axiosInstance.post(
    `/groups/${groupId}/settle`
  );
};
