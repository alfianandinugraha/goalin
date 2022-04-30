import supabase from "utils/vendors/supabase";

const getDetail = async (userId) => {
  const { data, error } = await supabase
    .from("users")
    .select("user_id, full_name, email")
    .eq("user_id", userId);

  if (error) throw new Error("Gagal mendapatkan user");

  if (!data.length) {
    const error = new Error("User tidak ditemukan");
    error.statusCode = 404;

    throw error;
  }

  const [user] = data;

  return {
    id: userId,
    fullName: user.full_name,
    email: user.email,
  };
};

const userServices = {
  get: getDetail,
};

export default userServices;
