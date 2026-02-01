import axiosInstance from "./axiosInstance";

export const fetchGroupExpenses = async (groupId) => {
  const response = await axiosInstance.get(
    `/groups/${groupId}/expenses`
  );
  return response.data;
};
export const createExpense = async (groupId, payload) => {
  const response = await axiosInstance.post(
    `/groups/${groupId}/expenses`,
    payload
  );
  return response.data;
};