import nc, { NextConnect } from "next-connect";
import { NextApiRequest, NextApiResponse } from "next";
import tokenMiddleware from "utils/middlewares/token";
import cors from "cors";

/**
 * @returns {NextConnect<NextApiRequest, NextApiResponse>}
 */
const connect = () => {
  return nc().use(cors());
};

/**
 * @returns {NextConnect<NextApiRequest, NextApiResponse>}
 */
const connectAuth = () => {
  return nc().use(cors()).use(tokenMiddleware);
};

export { connectAuth };
export default connect;
