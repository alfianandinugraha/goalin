import Ajv from "ajv";
import connect from "utils/helpers/connect";
import addFormats from "ajv-formats";
import response from "utils/constants/messages/response";
import { registerBodySchema } from "utils/constants/schemas/register";
import tokenServices from "services/token";
import authServices from "services/auth";

const ajv = new Ajv();
addFormats(ajv, { formats: ["email"] });

export default connect().post(async (req, res) => {
  const validate = ajv.compile(registerBodySchema);

  if (!validate(req.body)) {
    return res.status(400).json(response.INVALID_BODY);
  }

  try {
    const user = await authServices.register(req.body);
    const token = tokenServices.sign({ id: user.id });

    return res.json({
      message: "Pendaftaran berhasil",
      payload: {
        token,
      },
    });
  } catch (err) {
    return res.status(400).json({
      message: err.message,
      payload: {},
    });
  }
});
