import supabase from "utils/vendors/supabase";
import { v4 as uuidv4 } from "uuid";
import { RegisterBody } from "utils/constants/schemas/register";
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

const authServices = {
  register: registerUser,
};

export default authServices;
