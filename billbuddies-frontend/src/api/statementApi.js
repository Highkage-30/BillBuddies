import axiosInstance from "./axiosInstance";

export const fetchGroupStatement = async (groupId) => {
  const response = await axiosInstance.get(
    `/groups/${groupId}/statement`
  );
  return response.data;
};
