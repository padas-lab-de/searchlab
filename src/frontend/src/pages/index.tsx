"use client";
import React, { useState } from 'react';
import { useRouter } from 'next/router';

export default function Home() {
  const [showSurvey, setShowSurvey] = useState(false);
  const router = useRouter();

  const surveyQuestions = [
    {
      question_id: "english_level",
      question_text: "What is your level of English proficiency?",
      response_type: "scale",
      scale_min: 1,
      scale_max: 5,
      scale_labels: ["Very Low", "Low", "Moderate", "High", "Very High"],
    },
    {
      question_id: "age",
      question_text: "What is your age?",
      response_type: "integer",
    },
    {
      question_id: "education_level",
      question_text: "What is your highest level of education?",
      response_type: "choice",
      choices: ["High School", "Bachelor's Degree", "Master's Degree", "PhD", "Other"],
    },
    {
      question_id: "major",
      question_text: "What was your major area of study?",
      response_type: "text",
    },
    {
      question_id: "search_expertise",
      question_text: "Rate your expertise using web search engines on a scale of 1 to 5.",
      response_type: "scale",
      scale_min: 1,
      scale_max: 5,
    }
  ];

  const renderSurveyForm = () => {
    return surveyQuestions.map((question, index) => {
      switch (question.response_type) {
        case 'scale':
          return (
            <div key={index}>
              <h4 className="text-xl mb-4">{question.question_text}</h4>
              <input type="range" min={question.scale_min} max={question.scale_max} style={{ minWidth: '100%' }}  />
              <div className="flex justify-between mb-4">
                {question.scale_labels?.map((label, idx) => (
                  <span key={idx}>{label}</span>
                ))}
              </div>
            </div>
          );
        case 'integer':
          return (
            <div key={index}>
              <h4 className="text-xl mb-4">{question.question_text}</h4>
              <input className="input-questionnaire text-white block" type="number" />
            </div>
          );
        case 'choice':
          return (
            <div key={index}>
              <h4 className="text-xl mb-4">{question.question_text}</h4>
              <select className="input-questionnaire text-white block">
                {question.choices?.map((choice, idx) => (
                  <option key={idx} value={choice}>{choice}</option>
                ))}
              </select>
            </div>
          );
        case 'text':
          return (
            <div key={index}>
              <h4 className="text-xl mb-4">{question.question_text}</h4>
              <input className="input-questionnaire text-white block" type="text" />
            </div>
          );
        default:
          return null;
      }
    });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    router.push('/tasks?task_id=1');
  }

  return (
    <div className="flex h-screen bg-page-background bg-cover">
      <div className="w-1/2 flex flex-col items-center justify-center">
        {!showSurvey ? (
          <>
            <div className="text-center text-left-align mb-8 ml-10">
              <span className="block text-lg mb-2 join-text-color">USER SEARCH STUDY</span>
              <h1 className="text-5xl mb-4">Start a Search Session.</h1>
              <h4 className="text-xl mb-4">Click Get started to begin. First, complete a brief pre-task questionnaire.<br /> Then, perform the necessary searches and click End task when finished. </h4>
            </div>
            <div className="flex flex-col text-left-align">
              <input
                type="text"
                placeholder="Enter ID Code"
                className="input-custom mb-4 p-2 rounded text-white block"
              />
              <button
                className="mt-8 py-4 px-10 bg-blue-500 text-white rounded-full text-lg button-modified block"
                onClick={() => setShowSurvey(true)}
              >
                Get Started
              </button>
            </div>
          </>
        ) : (
          <>
            <div className="text-center text-left-align mb-8 ml-10">
              <span className="block text-lg mb-2 join-text-color">PRE-TASK QUESTIONNAIRE</span>
              <h1 className="text-5xl mb-4">Start a Search Session.</h1>
            </div>
            <form onSubmit={handleSubmit} className="survey-form">
              {renderSurveyForm()}
              <button type="submit" className="py-4 px-20 bg-blue-500 text-white rounded-full text-lg mt-4">
                Submit
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}