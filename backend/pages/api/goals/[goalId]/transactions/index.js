import Ajv from "ajv";
import response from "utils/constants/messages/response";
import { transactionBodySchema } from "utils/constants/schemas/transaction";
import { connectAuth } from "utils/helpers/connect";

const ajv = new Ajv();

export default connectAuth().post((req, res) => {
  const validate = ajv.compile(transactionBodySchema);

  if (!validate(req.body)) {
    return res.status(400).json(response.INVALID_BODY);
  }

  return res.json({
    message: "Berhasil menyimpan transaction",
    payload: {},
  });
});
