import axiosInstance from "./axiosInstance";

export const fetchGroups = async () => {
  const response = await axiosInstance.get("/groups");
  return response.data; // [] is valid
};
export const createGroup = async (payload) => {
  const response = await axiosInstance.post("/groups", payload);
  return response.data;
};