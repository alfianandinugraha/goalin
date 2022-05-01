import jwt from "jsonwebtoken";

const signToken = (body) => {
  return jwt.sign(body, process.env.NEXT_PUBLIC_PRIVATE_JWT_KEY, {
    expiresIn: "7d",
  });
};

const decodeToken = (token) => {
  return jwt.verify(token, process.env.NEXT_PUBLIC_PRIVATE_JWT_KEY);
};

const tokenServices = {
  sign: signToken,
  decode: decodeToken,
};

export default tokenServices;
