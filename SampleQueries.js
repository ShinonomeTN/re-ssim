db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            code: true
        }
    },
    {
        $group: {
            _id: '$code'
        }
    },
    {
        $group: {
            _id: 1,
            count: {
                $sum: 1
            }
        }
    }
]);

// Term infos
db.courseEntity.aggregate([
    {
        $project: {
            term: true
        }
    },
    {
        $group: {
            _id: "$term",
            courseCount: {
                $sum: 1
            }
        }
    },
    {
        $project: {
            "name": "$_id",
            "courseCount": "$courseCount",
            "_id": false
        }
    }
]);

// Course Type
db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            classType: "$lessons.classType"
        }
    },
    {
        $match: {
            term: "2014-2015学年第二学期"
        }
    },
    {$unwind: "$classType"},
    {
        $group: {
            _id: null,
            classType: {$addToSet: "$classType"}
        }
    }
]);

db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            timePoint: "$lessons.timePoint"
        }
    },
    {
        $match: {
            term: "2018-2019学年第一学期"
        }
    },
    {$unwind: "$timePoint"},
    {$unwind: "$timePoint"},
    {
        $group: {
            _id: null,
            maxWeek: {$max: "$timePoint.week"},
            minWeek: {$min: "$timePoint.week"}
        }
    }
]);

db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            timePoint: "$lessons.timePoint"
        }
    },
    {
        $match: {
            term: "2017-2018学年第二学期"
        }
    }
]);

// Courses
db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            code: true,
            name: true,
            unit: true,
            "lessons.classType": true,
            assessmentType: true
        }
    },
    {$match: {term: "2018-2019学年第一学期"}},
    {$unwind: "$lessons"},
    {
        $group: {
            _id: {
                term: "$term",
                code: "$code",
                name: "$name",
                unit: "$unit",
                assessmentType: "$assessmentType",
                classType: "$lessons.classType"
            },
            count: {$sum: 1}
        }
    }
]);

// Teachers
db.courseEntity.aggregate([
    {$project: {term: 1, "lessons.teacher": 1}},
    {$match: {term: "2018-2019学年第一学期"}},
    {$unwind: "$lessons"},
    {$group: {_id: null, teachers: {$addToSet: "$lessons.teacher"}}},
    {$project: {_id: false, teachers: true}}
]);

// Classes
db.courseEntity.aggregate([
    {$project: {term: true, "lessons.classAttend": true}},
    {$match: {term: "2017-2018学年第二学期"}},
    {$unwind: "$lessons"},
    {$group: {_id: "$lessons.classAttend"}},
    {$unwind: "$_id"},
    {$group: {_id: null, classes: {$addToSet: "$_id"}}},
    {$project: {_id: false, classes: true}}
]);

// Classes （match）
db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            "lessons.classAttend": true
        }
    },
    {$match: {term: "2017-2018学年第二学期"}},
    {$unwind: "$lessons"},
    {$group: {_id: "$lessons.classAttend"}},
    {$unwind: "$_id"},
    {$match: {_id: /^\d\d软件技术/}},
    {$group: {_id: null, classes: {$addToSet: "$_id"}}},
    {$project: {_id: false, classes: true}}
]);

// Week range
db.courseEntity.aggregate([
    {$project: {"lessons.timePoint": true}},
    {$unwind: "$lessons"},
    {$group: {_id: "$lessons.timePoint"}},
    {$unwind: "$_id"},
    {
        $group: {
            _id: null,
            maxWeek: {$max: "$_id.week"},
            minWeek: {$min: "$_id.week"}
        }
    },
    {$project: {_id: 0, max: "$maxWeek", min: "$minWeek"}}
]);

// Class Course
db.courseEntity.aggregate([
    {
        $project: {
            code: true,
            name: true,
            "lessons.classAttend": true,
            "lessons.classType": true
        }
    },
    {
        $match: {
            "lessons.classAttend": /^\d\d软件技术/,
            "lessons.classType": /^专业课/
        }
    },
    {$unwind: "$lessons"},
    {
        $group: {
            _id: {
                code: "$code",
                name: "$name",
                classType: "$lessons.classType",
                classAttend: "$lessons.classAttend"
            }
        }
    },
    {
        $project: {
            _id: false,
            code: "$_id.code",
            name: "$_id.name",
            classType: "$_id.classType",
            classAttend: "$_id.classAttend"
        }
    },
    {
        $sort: {
            name: 1
        }
    }
]);

db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            code: true,
            name: true,
            "lessons.teacher": true,
            "lessons.classAttend": true,
            "lessons.classType": true,
            "lessons.timePoint": true,
            "lessons.position": true
        }
    },
    {
        $match: {
            term: "2017-2018学年第二学期",
            "lessons.classAttend": {$in: ["17软件技术2班"]},
            "lessons.classType": {$nin: ["专业课/必修课"]}
        }
    },
    {$unwind: "$lessons"},
    {$unwind: "$lessons.timePoint"},
    {
        $match: {
            "lessons.timePoint.week": 19
        }
    },
    {
        $group: {
            _id: "$lessons.timePoint",
            lessons: {
                $addToSet: {
                    code: "$code",
                    name: "$name",
                    classType: "$lessons.classType",
                    teacher: "$lessons.teacher",
                    position: "$lessons.position"
                }
            }
        }
    },
    {
        $project: {
            _id: false,
            timePoint: "$_id",
            lessons: true
        }
    }
]);

db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            name: true,
            unit: true,
            "lessons.teacher": true,
            "lessons.classAttend": true,
            "lessons.timePoint": true,
            "lessons.classType": true
        }
    },
    {
        $match: {
            term: "2018-2019学年第一学期"
        }
    },
    {$unwind: "$lessons"},
    {$unwind: "$lessons.timePoint"},

    {
        $group: {
            _id: "$lessons.teacher",
            "lessons": {$sum: 1}
        }
    },
    {
        $sort: {
            lessons: -1
        }
    }
]);

// unit: "电子信息工程学院",
// "lessons.classType" : { $in : ["专业基础课/必修课", "双创课/限选课", "专业课/必修课","专业课/限选课"]},
// "lessons.classAttend": { $in : [/\d{2,4}软件技术.+/] }

db.courseEntity.aggregate([
    {
        $project: {
            term: true,
            name: true,
            unit: true,
            "lessons.teacher": true,
            "lessons.classAttend": true,
            "lessons.timePoint": true,
            "lessons.classType": true
        }
    },
    {
        $match: {
            term: "2018-2019学年第一学期",
            unit: "电子信息工程学院",
            "lessons.classType": {$in: ["专业基础课/必修课", "双创课/限选课", "专业课/必修课", "专业课/限选课"]},
            "lessons.classAttend": {$in: [/\d{2,4}软件技术.+/]}
        }
    },
    {$unwind: "$lessons"},
    {
        $group: {
            _id: null,
            "teachers": {$addToSet: "$lessons.teacher"}
        }
    },

    {
        $project: {
            _id: 0
        }
    }
]);