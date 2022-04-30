import jwt from "jsonwebtoken";

const signToken = (body) => {
  return jwt.sign(body, process.env.NEXT_PUBLIC_PRIVATE_JWT_KEY, {
    expiresIn: "7d",
  });
};

const tokenServices = {
  sign: signToken,
};

export default tokenServices;
