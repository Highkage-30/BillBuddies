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
export const deleteExpense = async (
  groupId,
  expenseId
) => {
  return axiosInstance.delete(
    `/groups/${groupId}/expenses/${expenseId}`
  );
};
export const uploadExpensesFile = (groupId, file) => {
  const formData = new FormData();
  formData.append("file", file);

  return axiosInstance.post(
    `/groups/${groupId}/expenses/upload`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );
};