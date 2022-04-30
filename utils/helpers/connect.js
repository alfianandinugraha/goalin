import nc, { NextConnect } from "next-connect";
import { NextApiRequest, NextApiResponse } from "next";

/**
 * @returns {NextConnect<NextApiRequest, NextApiResponse>}
 */
const connect = () => {
  return nc();
};

export default connect;
