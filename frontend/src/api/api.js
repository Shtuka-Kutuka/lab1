import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8080/api"
});

export const getUsers = () => API.get("/users");
export const createUser = (data) => API.post("/users", data);

export const getGoals = () => API.get("/goals");
export const createGoal = (data) => API.post("/goals", data);
export const updateGoal = (id, data) => API.put(`/goals/${id}`, data);
export const deleteGoal = (id) => API.delete(`/goals/${id}`);

export const getMoods = () => API.get("/moods");
export const createMood = (data) => API.post("/moods", data);
export const deleteMood = (id) => API.delete(`/moods/${id}`);
export const getMoodsByUser = (userId) => API.get(`/moods/user/${userId}`);

export const filterMoods = (userId, moodName) =>
    API.get(`/moods/complex?userId=${userId}&moodName=${moodName}`);

export const getTags = () => API.get("/tags");
export const createTag = (data) => API.post("/tags", data);

export const getMoodTypes = () => API.get("/mood-types");

export const createMoodType = (data) => API.post("/mood-types", data);

export const deleteMoodType = (id) => API.delete(`/mood-types/${id}`);