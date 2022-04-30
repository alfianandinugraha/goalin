import Ajv from "ajv";
import { loginBodySchema } from "utils/constants/schemas/login";
import connect from "utils/helpers/connect";
import addFormats from "ajv-formats";
import response from "utils/constants/messages/response";

const ajv = new Ajv();
addFormats(ajv, { formats: ["email"] });

export default connect().post(async (req, res) => {
  const validate = ajv.compile(loginBodySchema);

  if (!validate(req.body)) {
    return res.status(400).json(response.INVALID_BODY);
  }

  return res.json({
    message: "Login berhasil",
    payload: {
      token: "11111",
    },
  });
});
