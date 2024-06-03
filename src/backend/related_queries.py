import os
from backend.constants import ChatModel
from backend.prompts import RELATED_QUESTION_PROMPT
import groq
import instructor
import openai
from backend.constants import model_mappings
from backend.schemas import RelatedQueries, SearchResult
from dotenv import load_dotenv

load_dotenv()


OLLAMA_HOST = os.environ.get("OLLAMA_HOST", "http://localhost:11434")


def instructor_client(model: ChatModel) -> instructor.AsyncInstructor:
    if model in [
        ChatModel.GPT_3_5_TURBO,
        ChatModel.GPT_4o,
    ]:
        return instructor.from_openai(openai.AsyncOpenAI())
    elif model in [
        ChatModel.LOCAL_GEMMA,
        ChatModel.LOCAL_LLAMA_3,
        ChatModel.LOCAL_MISTRAL,
    ]:
        return instructor.from_openai(
            openai.AsyncOpenAI(
                base_url=f"{OLLAMA_HOST}/v1",
                api_key="ollama",
            ),
            mode=instructor.Mode.JSON,
        )
    elif model == ChatModel.LLAMA_3_70B:
        return instructor.from_groq(groq.AsyncGroq(), mode=instructor.Mode.JSON)
    else:
        raise ValueError(f"Unknown model: {model}")


async def generate_related_queries(
    query: str, search_results: list[SearchResult], model: ChatModel
) -> list[str]:
    context = "\n\n".join([f"{str(result)}" for result in search_results])
    client = instructor_client(model)
    model_name = model_mappings[model]

    related = await client.chat.completions.create(
        model=model_name,
        response_model=RelatedQueries,
        messages=[
            {
                "role": "user",
                "content": RELATED_QUESTION_PROMPT.format(query=query, context=context),
            },
        ],
    )

    return [query.lower().replace("?", "") for query in related.related_queries]
