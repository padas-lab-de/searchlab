"use client";
import React from 'react';
import { useRouter } from 'next/router';

export default function EndSession() {
  const router = useRouter();

  const satisfactionQuestions = [
    {
      question_id: "stress_level",
      question_text: "How stressed are you feeling right now?",
      response_type: "scale",
      scale_min: 1,
      scale_max: 5,
      scale_labels: ["Not at all", "Slightly", "Moderately", "Very", "Extremely"],
    },
    {
      question_id: "distraction_level",
      question_text: "How distracted are you right now?",
      response_type: "scale",
      scale_min: 1,
      scale_max: 5,
      scale_labels: ["Not at all", "Slightly", "Moderately", "Very", "Extremely"],
    },
    {
      question_id: "search_layout_usefulness",
      question_text: "How useful was the search layout?",
      response_type: "scale",
      scale_min: 0,
      scale_max: 3,
      scale_labels: ["Not at all", "Somewhat", "Fairly", "Very useful"],
    },
    {
      question_id: "querying_satisfaction",
      question_text: "How satisfied are you with the querying process?",
      response_type: "scale",
      scale_min: 1,
      scale_max: 5,
      scale_labels: ["Not at all", "Slightly", "Moderately", "Very", "Extremely"],
    },
    {
      question_id: "session_satisfaction",
      question_text: "How satisfied are you with the whole search session?",
      response_type: "scale",
      scale_min: 1,
      scale_max: 5,
      scale_labels: ["Not at all", "Slightly", "Moderately", "Very", "Extremely"],
    },

  ];

  const renderSatisfactionForm = () => {
    return satisfactionQuestions.map((question, index) => {
      switch (question.response_type) {
        case 'scale':
          return (
            <div key={index} className="mb-6">
              <h4 className="text-xl mb-4">{question.question_text}</h4>
              <input type="range" min={question.scale_min} max={question.scale_max} style={{ minWidth: '100%' }} />
              <div className="flex justify-between">
                {question.scale_labels?.map((label, idx) => (
                  <span key={idx}>{label}</span>
                ))}
              </div>
            </div>
          );
          
        default:
          return null;
      }
    });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    console.log("Form submitted"); 
    router.push('/'); 
  };

  return (
    <div className="flex h-screen bg-page-background bg-cover">
      <div className="w-1/2 flex flex-col items-center justify-center">
        <div className="text-center text-left-align mb-8 ml-10">
          <span className="block text-lg mb-2 join-text-color">POST-SESSION QUESTIONNAIRE</span>
          <h1 className="text-5xl mb-4">Your Feedback Matters</h1>
          <h4 className="text-xl mb-4">Please, complete the following questionnaire.</h4>
        </div>
        <form onSubmit={handleSubmit} className="survey-form">
          {renderSatisfactionForm()}
          <button type="submit" className="py-4 px-20 bg-blue-500 text-white rounded-full text-lg mt-4">
            Submit
          </button>
        </form>
      </div>
    </div>
  );
}