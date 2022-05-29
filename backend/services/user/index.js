import postgreErrors from "utils/constants/messages/postgre-errors";
import supabase from "utils/vendors/supabase";

/**
 * @typedef UpdateUserBody
 * @property {string} fullName
 * @property {string} email
 * @property {string} userId
 */

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

/**
 * @param {UpdateUserBody} body
 */
const updateUser = async (body) => {
  const { error } = await supabase
    .from("users")
    .update({
      full_name: body.fullName,
      email: body.email,
    })
    .match({
      user_id: body.userId,
    });

  if (error) {
    if (error.code === postgreErrors.UNIQUE_VIOLATION)
      throw new Error("Email sudah digunakan");
    throw new Error("Gagal memperbarui user");
  }

  return {};
};

const userServices = {
  get: getDetail,
  update: updateUser,
};

export default userServices;
