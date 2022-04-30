import tokenServices from "services/token";
import userServices from "services/user";

const tokenMiddleware = async (req, res, next) => {
  const [, token] = req.headers.authorization.split(" ");

  if (!token) {
    return res.status(400).json({
      message: "Invalid token",
      payload: {},
    });
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
