import Ajv from "ajv";
import userServices from "services/user";
import { updateUserBodySchema } from "utils/constants/schemas/user";
import { connectAuth } from "utils/helpers/connect";

const ajv = new Ajv();

export default connectAuth()
  .get(async (req, res) => {
    return res.json({
      message: "Berhasil mendapatkan user",
      payload: { ...req.user },
    });
  })
  .put(async (req, res) => {
    const validate = ajv.compile(updateUserBodySchema);

    if (!validate(req.body)) {
      return res.status(400).json(response.INVALID_BODY);
    }

    const userId = req.user.id;

    try {
      await userServices.update({
        email: req.body.email,
        fullName: req.body.fullName,
        userId: userId,
      });

      return res.json({
        message: "Berhasil memperbarui user",
        payload: {},
      });
    } catch (err) {
      return res.status(err.statusCode ?? 400).json({
        message: err.message,
        payload: {},
      });
    }
  });
