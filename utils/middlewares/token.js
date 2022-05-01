import tokenServices from "services/token";
import userServices from "services/user";
import response from "utils/constants/messages/response";

const tokenMiddleware = async (req, res, next) => {
  const { authorization } = req.headers;

  if (!authorization) {
    return res.status(400).json(response.INVALID_TOKEN);
  }

  const [, token] = authorization.split(" ");

  if (!token) {
    return res.status(400).json(response.INVALID_TOKEN);
  }

  try {
    const userDecoded = tokenServices.decode(token);
    const user = await userServices.get(userDecoded.id);
    req.user = { ...user };

    return next();
  } catch (err) {
    return res.status(err.statusCode ?? 400).json({
      message: err.message,
      payload: {},
    });
  }
};

export default tokenMiddleware;
