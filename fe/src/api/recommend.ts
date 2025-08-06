import axiosInstance from ".";

export const getRecommend = async (imageFile: File) => {
    const formData = new FormData();
    formData.append('image', imageFile);
    try {
        const response = await axiosInstance.post(
            `/api/products/recommendations`,
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            }
        );
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};