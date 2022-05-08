import supabase from "utils/vendors/supabase";
import { v4 as uuidv4 } from "uuid";
import { RegisterBody } from "utils/constants/schemas/register";
import { LoginBody } from "utils/constants/schemas/login";
import postgreErrors from "utils/constants/messages/postgre-errors";
import hashServices from "services/hash";

/**
 * @param {RegisterBody} body
 */
const registerUser = async (body) => {
  const id = uuidv4();
  const { error } = await supabase.from("users").insert({
    user_id: id,
    full_name: body.fullName,
    email: body.email,
    password: hashServices.hash(body.password),
  });

  if (error) {
    switch (error.code) {
      case postgreErrors.UNIQUE_VIOLATION:
        throw new Error("Email sudah terdaftar");
      default:
        throw new Error("Failed register user");
    }
  }

  return {
    id,
    ...body,
  };
};

/**
 * @param {LoginBody} body
 */
const loginUser = async (body) => {
  const { data, error } = await supabase
    .from("users")
    .select("email, password, user_id")
    .eq("email", body.email);

  if (error) throw new Error("Login gagal");

  if (!data.length) throw new Error("User tidak ditemukan");

  const [user] = data;

  const isValid = hashServices.compare(body.password, user.password);

  if (!isValid) throw new Error("User tidak ditemukan");

  return {
    id: user.user_id,
  };
};

const authServices = {
  register: registerUser,
  login: loginUser,
};

export default authServices;
