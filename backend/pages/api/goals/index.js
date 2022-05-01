import Ajv from "ajv";
import { connectAuth } from "utils/helpers/connect";
import { createGoalSchema } from "utils/constants/schemas/goal";
import response from "utils/constants/messages/response";
import goalServices from "services/goal";

const ajv = new Ajv();

export default connectAuth().post(async (req, res) => {
  const validate = ajv.compile(createGoalSchema);

  if (!validate(req.body)) {
    return res.status(400).json(response.INVALID_BODY);
  }

  try {
    const goal = await goalServices.create({
      userId: req.user.id,
      ...req.body,
    });

    return res.json({
      message: "Berhasil menyimpan goal",
      payload: goal,
    });
  } catch (err) {
    return res.status(err.statusCode ?? 400).json({
      message: err.message,
      payload: {},
    });
  }
});
