import nc, { NextConnect } from "next-connect";
import { NextApiRequest, NextApiResponse } from "next";
import tokenMiddleware from "utils/middlewares/token";

/**
 * @returns {NextConnect<NextApiRequest, NextApiResponse>}
 */
const connect = () => {
  return nc();
};

/**
 * @returns {NextConnect<NextApiRequest, NextApiResponse>}
 */
const connectAuth = () => {
  return nc().use(tokenMiddleware);
};

export { connectAuth };
export default connect;
