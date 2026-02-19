import axiosInstance from "./axiosInstance";

export const fetchMembers = async () => {
  const response = await axiosInstance.get("/members");
  return response.data; // [] is valid
};
